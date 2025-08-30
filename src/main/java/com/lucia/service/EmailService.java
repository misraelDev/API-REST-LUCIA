package com.lucia.service;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final Resend resend;
    

    
    public EmailService() {
        // Inicializamos Resend con la API key directamente
        String apiKey = "re_5sNeS3EM_PTGiUEQg8SgF31moHnwBAZzX";
        this.resend = new Resend(apiKey);
        logger.info("Resend configurado con API key");
    }
    
    /**
     * Envía un email de contacto usando Resend
     */
    public boolean sendContactEmail(String name, String email, String phone, String need, String message) {
        if (resend == null) {
            logger.error("Resend no está configurado");
            return false;
        }
        
        try {
            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                .from("Lucia Voice Agent <onboarding@resend.dev>")
                .to("mdelrey@sarexlabs.com")
                .subject("Nuevo mensaje de contacto de " + name)
                .html(generateContactEmailHTML(name, email, phone, need, message))
                .build();
            
            CreateEmailResponse response = resend.emails().send(emailOptions);
            
            if (response != null && response.getId() != null) {
                logger.info("Email enviado exitosamente con ID: {}", response.getId());
                return true;
            } else {
                logger.error("Error al enviar email: respuesta vacía");
                return false;
            }
            
        } catch (ResendException e) {
            logger.error("Error de Resend al enviar email: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al enviar email: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera el HTML del email de contacto
     */
    private String generateContactEmailHTML(String name, String email, String phone, String need, String message) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nuevo mensaje de contacto - Lucia Voice Agent</title>
                <link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap' rel='stylesheet'>
            </head>
            <body style="background-color: #f8fafc; margin: 0; padding: 20px; font-family: 'Inter', 'Segoe UI', sans-serif; line-height: 1.6;">
                <div style="max-width: 560px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; border: 1px solid #d1d5db; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); overflow: hidden;">
                    
                    <!-- Header -->
                    <div style="background-color: #462BDD; padding: 20px 24px; text-align: center;">
                        <h1 style="color: #ffffff; font-size: 24px; font-weight: 700; margin: 0; letter-spacing: -0.025em;">
                            Nueva solicitud de contacto
                        </h1>
                    </div>

                    <!-- Content -->
                    <div style="padding: 32px 24px;">
                        
                        <!-- Greeting -->
                        <div style="margin-bottom: 24px;">
                            <p style="color: #374151; font-size: 16px; margin: 0 0 8px 0; font-weight: 500;">
                                Hola Mdelrey,
                            </p>
                            <p style="color: #6b7280; font-size: 14px; margin: 0;">
                                Has recibido un nuevo mensaje desde tu sitio web.
                            </p>
                        </div>

                        <!-- Contact Info Card -->
                        <div style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; margin-bottom: 20px;">
                            <h3 style="color: #111827; font-size: 16px; font-weight: 600; margin: 0 0 16px 0;">
                                Información del contacto
                            </h3>
                            
                            <div style="space-y: 12px;"> 
                                <div style="margin-bottom: 12px;">
                                    <span style="color: #6b7280; font-size: 13px; font-weight: 500; display: inline-block; width: 70px;">Nombre:</span>
                                    <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                </div>
                                
                                <div style="margin-bottom: 12px;">
                                    <span style="color: #6b7280; font-size: 13px; font-weight: 500; display: inline-block; width: 70px;">Email:</span>
                                    <a href="mailto:%s" style="color: #3b82f6; font-size: 14px; text-decoration: none; font-weight: 500;">%s</a>
                                </div>
                                
                                %s
                                
                                %s
                            </div>
                        </div>
                        
                        <!-- Message Card -->
                        <div style="background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                            <h4 style="color: #111827; font-size: 14px; font-weight: 600; margin: 0 0 12px 0;">
                                Mensaje
                            </h4>
                            <div style="color: #374151; font-size: 14px; line-height: 1.6; margin: 0; white-space: pre-wrap;">%s</div>
                        </div>
                        
                        <!-- Action Button -->
                        <div style="text-align: center; margin: 32px 0;">
                            <a href="mailto:%s?subject=Re: Contacto desde Lucia Voice Agent&body=Hola %s,%%0A%%0AGracias por contactarme..." 
                               style="background-color: #462BDD; 
                                      color: #ffffff; 
                                      padding: 12px 24px; 
                                      font-size: 14px; 
                                      font-weight: 600; 
                                      border: none; 
                                      border-radius: 6px; 
                                      text-decoration: none; 
                                      display: inline-block; 
                                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                                      transition: all 0.2s;">
                                Responder al contacto
                            </a>
                        </div>

                    </div>

                    <!-- Footer -->
                    <div style="background-color: #f9fafb; padding: 20px 24px; border-top: 1px solid #e5e7eb;">
                        <p style="color: #6b7280; font-size: 12px; margin: 0 0 8px 0; text-align: center;">
                            Este mensaje fue enviado desde el formulario de contacto de Lucia Voice Agent
                        </p>
                        <p style="color: #9ca3af; font-size: 11px; margin: 0; text-align: center;">
                            © 2024 Lucia Voice Agent. Todos los derechos reservados.
                        </p>
                    </div>

                </div>
            </body>
            </html>
            """.formatted(
                name,
                email, email,
                phone != null ? """
                    <div style="margin-bottom: 12px;">
                        <span style="color: #6b7280; font-size: 13px; font-weight: 500; display: inline-block; width: 70px;">Teléfono:</span>
                        <a href="tel:%s" style="color: #059669; font-size: 14px; text-decoration: none; font-weight: 500;">%s</a>
                    </div>
                    """.formatted(phone, phone) : "",
                need != null ? """
                    <div style="margin-bottom: 12px;">
                        <span style="color: #6b7280; font-size: 13px; font-weight: 500; display: inline-block; width: 70px;">Necesidad:</span>
                        <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                    </div>
                    """.formatted(need) : "",
                message,
                email, name
            );
    }
}
