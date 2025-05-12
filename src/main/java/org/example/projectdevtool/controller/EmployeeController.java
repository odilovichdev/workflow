package org.example.projectdevtool.controller;

import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.ProfileRequestDto;
import org.example.projectdevtool.dto.ResponseDto;
import org.example.projectdevtool.entity.Profile;
import org.example.projectdevtool.service.EmailService;
import org.example.projectdevtool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emp")
@AllArgsConstructor
public class EmployeeController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/invite")
    public ResponseEntity<ResponseDto> addEmployeesList(@RequestBody List<String> emails){
        String subject = "WorkFlow invitation";
        String link = "";
        String text = "you have been invited to WorkFlow \nregister via link: " + link;
        emailService.sendEmailInvitation(emails, subject, text);
        return ResponseEntity.ok(new ResponseDto("sent successfully",true));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Profile>> getAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/findBy/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable("email") String email){
        return ResponseEntity.ok(userService.findByEmail(email));
    }

//    @PostMapping("/fill-profile")
//    public ResponseEntity<?> fillProfile(@RequestBody ProfileRequestDto dto){
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//         return ResponseEntity.ok(userService.fillProfile(dto, userDetails.getUsername()));
//    }

    @PostMapping("/fill-profile")
    public ResponseEntity<?> fillProfile(@RequestBody ProfileRequestDto dto){
        return ResponseEntity.ok(userService.fillProfile(dto));
    }

    @GetMapping("/get-profile")
    public ResponseEntity<?> getMyProfile(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(userService.myProfile(userDetails.getUsername()));
    }

    @PostMapping("/history/{id}")
    public ResponseEntity<?> getHistory(@PathVariable Long id){
        return ResponseEntity.ok(userService.getHistory(id));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.findById(id));
    }
}
