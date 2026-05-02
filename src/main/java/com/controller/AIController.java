package com.controller;

import com.dto.request.AIRequest;
import com.dto.request.ApiResponse;
import com.dto.response.AIResponse;
import com.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AIController {
    AIService aiService;

    @PostMapping("/generate-content")
    public ApiResponse<AIResponse> getContent(@RequestBody AIRequest request) {
        System.out.println(request.toString());
        String promp = "từ đoạn này hãy tạo thêm sau đó khoảng 10 đến 20 từ sao cho hợp lý nhất có thể, chú ý chỉ được chèn thêm, không chỉnh sửa nội dung gốc, chỉ tra về nội dung không cần câu dẫn, trả về 1 phương án duy nhất: " + request.getMessage();
        return ApiResponse.<AIResponse>builder()
                .code(200)
                .message("get generate content successfully")
                .result(aiService.generateContent(promp))
                .build();
    }
}
