package com.arqui.alquilaya.config;
import java.time.Clock;
import org.springframework.context.annotation.*;

@Configuration
public class RelojConfig {
    @Bean public Clock reloj() { return Clock.systemDefaultZone(); }
}
