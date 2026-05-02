package com.service;

import com.client.OutboundIdentityClient;
import com.client.OutboundUserClient;
import com.dto.request.*;
import com.dto.response.*;
import com.entity.InvalidatedToken;
import com.entity.Role;
import com.entity.User;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.repository.InvalidatedTokenRepository;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository  invalidatedTokenRepository;
    RoleRepository roleRepository;
    private final OutboundUserClient outboundUserClient;
    private final OutboundIdentityClient outboundIdentityClient;
    @NonFinal
    @Value("${outbound.identity.client-id}")
    String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    String CLIENT_SECRET;

    //@NonFinal
    //@Value("${outbound.identity.redirect-uri}")
    String REDIRECT_URI = "postmessage";

    //đánh dấu không inject vào contructor
    @NonFinal
    protected static final String SIGNER_KEY = "ZYWT4y/G+dF+z31xQSIMo1rrCaEiuBwpbCmnewCGO4E=";

    @NonFinal
    protected static final long ACCESS_TOKEN_EXPIRY = 300; // 5 phút = 300 giây
    @NonFinal
    protected static final long REFRESH_TOKEN_EXPIRY = 86400; // 1 ngày = 86400 giây

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean authenticated =  passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = this.generateToken(user, ACCESS_TOKEN_EXPIRY);
        String refreshToken = this.generateToken(user, REFRESH_TOKEN_EXPIRY);

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);   // Quan trọng: Chặn JavaScript truy cập
        cookie.setSecure(false);    // Để true nếu dùng HTTPS
        cookie.setPath("/");        // Áp dụng cho toàn bộ domain
        // Đồng bộ MaxAge của Cookie với Token (1 ngày)
        cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRY); // Sống trong 7 ngày

        assert response != null;
        response.addCookie(cookie);
        boolean isAuthenticated = true;
        return new AuthenticationResponse(token, isAuthenticated);
    }


    //ham xu ly login voi google
    public AuthenticationResponse authenticateGoogle(String code) {
        // Bước A: Đổi Code lấy AccessToken của Google
        ExchangeTokenResponse response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType("authorization_code")
                .build());

        // Bước B: Dùng AccessToken đó để lấy thông tin User từ Google
        OutboundUserResponse userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        // Bước C: Tìm user trong DB, nếu không có thì đăng ký mới (Social Login)

        Role userRole = roleRepository.findById("user")
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .roles(userRole)
                        .email(userInfo.getEmail())
                        .username(userInfo.getEmail()) // Dùng email làm username
                        .password("") // Password trống vì login qua Google
                        .fullName(userInfo.getFamilyName())
                        .build()));
        System.out.print("thay user trong DB" + user);
        // Bước D: Tạo JWT của riêng hệ thống mình trả về cho Frontend
        String token = generateToken(user, ACCESS_TOKEN_EXPIRY);
        System.out.println("token" + token);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }


    public ApiResponse<Object> logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.getToken(), false);

        String jti = signedJwt.getJWTClaimsSet().getJWTID();

        Date expiry = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiry)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
        return ApiResponse.builder()
                .code(200)
                .message("logout successfully")
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        System.out.println(request.getToken());
        String token = request.getToken();
        try {
            SignedJWT signJwt = verifyToken(token, false);

            return IntrospectResponse.builder()
                    .valid(true)
                    .build();


        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateToken (User user, long expiryInSeconds){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256); //256 cho 32 bit va 512 cho 64 bit
        System.out.println("tao token");
        //các thành phần trong 1 jwt
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(user.getUsername())
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(
                        Instant.now().plusSeconds(expiryInSeconds).toEpochMilli()
                ))

                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        System.out.println("co payload " + payload);
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return  jwsObject.serialize();
        } catch (JOSEException e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        log.info(">>> [DEBUG] Bắt đầu verifyToken (isRefresh: {})", isRefresh);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String jti = signedJWT.getJWTClaimsSet().getJWTID();

        boolean verified = signedJWT.verify(verifier);
        log.info(">>> [DEBUG] Chữ ký hợp lệ: {}", verified);

        boolean isExpired = expiryTime.before(new Date());
        log.info(">>> [DEBUG] Token hết hạn chưa: {} (Hết hạn lúc: {})", isExpired, expiryTime);

        // LOGIC CHỐT CHẶN PHẲNG LÌ THƯA ÔNG CHỦ:
        // 1. Chữ ký sai -> Đuổi ngay.
        // 2. Không phải luồng Refresh MÀ token hết hạn -> Đuổi ngay.
        if (!verified || (!isRefresh && isExpired)) {
            log.error(">>> [STOP] Token không hợp lệ hoặc đã hết hạn trong luồng thông thường!");
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        // 3. Nếu là Refresh mà hết hạn -> Cho qua để đổi thẻ mới thưa ông chủ.
        if (isRefresh && isExpired) {
            log.info(">>> [PASS] Chấp nhận token hết hạn vì đây là luồng Refresh.");
        }

        if (invalidatedTokenRepository.existsById(jti)) {
            log.error(">>> [STOP] Token nằm trong Blacklist!");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public RefreshResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        log.info(">>> [START] Bắt đầu luồng Refresh Token thưa ông chủ...");

        try {
            // TRUYỀN TRUE VÀO ĐÂY THƯA ÔNG CHỦ!
            log.info(">>> [Step 1] Gọi verifyToken với chế độ REFRESH (isRefresh = true)");
            var signJWT = verifyToken(request.getToken(), true);

            var jit = signJWT.getJWTClaimsSet().getJWTID();
            var expireTime = signJWT.getJWTClaimsSet().getExpirationTime();

            log.info(">>> [Step 2] Vô hiệu hóa token cũ (Blacklist JTI: {})", jit);
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .expiryTime(expireTime)
                    .id(jit)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            var userName = signJWT.getJWTClaimsSet().getSubject();
            log.info(">>> [Step 3] Tìm kiếm User: {}", userName);

            var user = userRepository.findByUsername(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            log.info(">>> [Step 4] Tạo Access Token mới cho ông chủ...");
            var token = generateToken(user, ACCESS_TOKEN_EXPIRY);

            log.info(">>> [DONE] Refresh thành công! Hệ thống ViecS lại phẳng lì thưa ông chủ!");
            return RefreshResponse.builder()
                    .token(token)
                    .isAuthenticated(true)
                    .build();

        } catch (AppException e) {
            log.error(">>> [FAILED] Lỗi nghiệp vụ khi refresh: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error(">>> [CRITICAL] Lỗi hệ thống: ", e);
            throw e;
        }
    }

    private String buildScope(User user){
        StringJoiner scopes =  new StringJoiner(" ");
            scopes.add((CharSequence) user.getRoles().getName());
        return scopes.toString();

    }


}
