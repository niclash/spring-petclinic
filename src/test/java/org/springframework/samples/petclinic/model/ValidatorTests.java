package org.springframework.samples.petclinic.model;

import java.util.Locale;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.zest.api.value.ValueBuilder;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.test.AbstractZestTest;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test to make sure that Bean Validation is working
 */
public class ValidatorTests extends AbstractZestTest
{

    private Validator createValidator()
    {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }

    @Test
    public void shouldNotValidateWhenFirstNameEmpty()
    {

        LocaleContextHolder.setLocale( Locale.ENGLISH );
        ValueBuilder<Person> builder = valueBuilderFactory.newValueBuilder( Person.class );
        builder.prototype().firstName().set("");
        builder.prototype().lastName().set("smith");
        Person person = builder.newInstance();

        Validator validator = createValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate( person );

        assertThat( constraintViolations.size() ).isEqualTo( 1 );
        ConstraintViolation<Person> violation = constraintViolations.iterator().next();
        assertThat( violation.getPropertyPath().toString() ).isEqualTo( "firstName" );
        assertThat( violation.getMessage() ).isEqualTo( "may not be empty" );
    }

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.values( Person.class );
    }
}
