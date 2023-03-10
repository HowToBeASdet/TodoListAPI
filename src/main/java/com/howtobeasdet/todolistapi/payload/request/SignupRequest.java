package com.howtobeasdet.todolistapi.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String name;
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;
  private Integer age;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  public String getName() {
    return name;
  }

  public void setName(String username) {
    this.name = username;
  }
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
