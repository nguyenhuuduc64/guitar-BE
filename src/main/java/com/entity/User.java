package com.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE) //cac field khong co type thi se mac dinh la private
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String username;
    String password;
    String fullName;
    String email;
    @ManyToOne
    Role roles; //trong 1 mang role chi co 1 role la duy nhat, khong bi trung


}
