package farias.blockchain.domain;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*
  Allows to access Spring beans from non-managed contexts
  Completely copied from https://confluence.jaytaala.com/display/TKB/Super+simple+approach+to+accessing+Spring+beans+from+non-Spring+managed+classes+and+POJOs
 */

@Component
public class SpringContext implements ApplicationContextAware {

  private static ApplicationContext context;

  /**
   * Returns the Spring managed bean instance of the given class type if it exists.
   * Returns null otherwise.
   * @param beanClass
   * @return
   */
  public static <T extends Object> T getBean(Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {

    // store ApplicationContext reference to access required beans later on
    SpringContext.context = context;
  }
}
