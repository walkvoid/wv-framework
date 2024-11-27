package com.github.walkvoid.wvframework.tests.utils;

import com.github.walkvoid.wvframework.utils.ValidationUtils;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;


/**
 * @author walkvoid
 * @date 2024/11/26
 * @description: HibernateValidationUtilsTest
 * @version: 1.0.0
 */

public class ValidationUtilsTest {


    @Test
    public void validateTest1(){
        ValidationPojo validationPojo = new ValidationPojo();
        validationPojo.setName("walkvoid");
        ValidationUtils.validate(validationPojo);
    }




    public static class ValidationPojo implements Serializable {
        @NotNull()
        private Long id;
        @NotEmpty()
        private String name;

        public Long getId() {return id;}
        public void setId(Long id) {this.id = id;}
        public String getName() {return name;}
        public void setName(String name) {this.name = name;}
    }



}
