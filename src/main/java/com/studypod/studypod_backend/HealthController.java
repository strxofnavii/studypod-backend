   package com.studypod.studypod_backend;

   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;

   @RestController
   public class HealthController {
       @GetMapping("/health")
       public String health() {
           return "Study Pod backend is running";
       }
   }