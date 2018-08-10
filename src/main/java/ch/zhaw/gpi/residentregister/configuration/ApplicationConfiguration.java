package ch.zhaw.gpi.residentregister.configuration;

import ch.zhaw.gpi.residentregister.controller.ResidentRegisterController;
import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguriert verschiedene Aspekte der Spring-Application
 * Es werden Beans initialisiert, welche zur Laufzeit benötigt werden
 */
@Configuration
public class ApplicationConfiguration {
    
    

    /**
     * H2-Konsolen-Servlet-Registrierung
     * Stellt sicher, das die H2-Konsole über http://localhost:8080/console
     * aufrufbar ist Die H2-Konsole wird verwendet, um auf die Camunda-Datenbank
     * per GUI zugreifen zu können.
     *
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/console/*");
        return registration;
    }
    
    /**
     * Initialisierung des Webservice-Controllers
     * @return 
     */
    @Bean
    public ResidentRegisterController personenRegisterController() {
        return new ResidentRegisterController();
    }   
}
