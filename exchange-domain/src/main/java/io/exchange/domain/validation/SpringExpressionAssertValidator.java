package io.exchange.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpringExpressionAssertValidator implements ConstraintValidator<SpringExpressionAssert, Object> {

    private Expression exp;

    public void initialize(SpringExpressionAssert annotation) {
        ExpressionParser parser = new SpelExpressionParser();
        exp = parser.parseExpression(annotation.value());
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return exp.getValue(value, Boolean.class);
    }

}