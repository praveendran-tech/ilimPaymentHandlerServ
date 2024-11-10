package com.github.ilim.backend.ilimPaymentHandlerServ.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotNull(message = "courseId is required")
    private Long courseId;

    @NotBlank(message = "courseName is required")
    private String courseName;

    @NotBlank(message = "courseDescription is required")
    private String courseDescription;

    @NotNull(message = "coursePrice is required")
    @Min(value = 1, message = "coursePrice must be at least 0.01")
    private Double coursePrice;

    @NotBlank(message = "currency is required")
    private String currency;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(Double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}