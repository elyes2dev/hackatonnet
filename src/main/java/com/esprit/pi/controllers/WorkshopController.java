package com.esprit.pi.controllers;


import com.esprit.pi.entities.User;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.IWorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workshops")
public class WorkshopController {

    @Autowired
    private IWorkshopService workshopService;

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private UserRepository userRepository; // Ensure this repository is injected

    @PostMapping("/add")
    public ResponseEntity<Workshop> addWorkshop(@RequestBody Workshop workshop) {
        if (workshop.getUser() == null) {
            return ResponseEntity.badRequest().body(null); // No user provided
        }

        // Fetch the user from the database to make sure it's already persisted
        User user = userRepository.findById(workshop.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set the user to the workshop
        workshop.setUser(user);

        // Save the workshop
        Workshop savedWorkshop = workshopService.addWorkshop(workshop);
        // Send email to the creator
    //    sendWorkshopCreatedEmail(savedWorkshop,user);
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.println(user.getEmail());
        System.out.println(savedWorkshop.getName());

        return ResponseEntity.ok(savedWorkshop);
    }

 //   private void sendWorkshopCreatedEmail(String recipientEmail, String workshopTitle) {
 //       SimpleMailMessage message = new SimpleMailMessage();
 //       message.setTo(recipientEmail);
 //       message.setSubject("🎉 Workshop Published");
  //      message.setText("Your workshop \"" + workshopTitle + "\" has been published successfully!");

   //     mailSender.send(message);
  //  }

    private void sendWorkshopCreatedEmail(Workshop workshop, User user) {
        String recipientEmail = user.getEmail();
        String userFullName = user.getName() + " " + user.getLastname();
        String workshopTitle = workshop.getName();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("✅ Your Workshop Has Been Officially Published!");

        String emailText = """
            Hi %s,

            Congratulations! 🎉

            Your workshop titled "%s" has been officially published on our platform. We're thrilled to have contributors like you who are passionate about sharing knowledge and building a stronger learning community.

            🧠 Thank you for taking the time to create and submit your workshop. We appreciate your contribution and look forward to the positive impact it will have on others.

            If you have any questions or would like to update your workshop later, feel free to log in and make edits anytime.

            Best regards,  
            The Library Team 📚
            """.formatted(userFullName, workshopTitle);

        message.setText(emailText);

        mailSender.send(message);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Workshop> getWorkshopById(@PathVariable Long id) {
        return workshopService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Workshop> getAllWorkshops() {
        return workshopService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteWorkshop(@PathVariable Long id) {
        workshopService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workshop> updateWorkshop(@PathVariable Long id, @RequestBody Workshop workshop) {
        try {
            Workshop updated = workshopService.updateWorkshop(id, workshop);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // If workshop not found
        }
    }
}

