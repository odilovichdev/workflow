package org.example.projectdevtool.service;

import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.EmployeeHistory;
import org.example.projectdevtool.dto.ProfileRequestDto;
import org.example.projectdevtool.dto.RegisterRequest;
import org.example.projectdevtool.entity.Profile;
import org.example.projectdevtool.entity.Project;
import org.example.projectdevtool.entity.Task;
import org.example.projectdevtool.entity.Users;
import org.example.projectdevtool.repo.ProfileRepo;
import org.example.projectdevtool.repo.ProjectRepo;
import org.example.projectdevtool.repo.TaskRepo;
import org.example.projectdevtool.repo.UsersRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepo profileRepo;
    private final ProjectRepo projectRepo;
    private final TaskRepo taskRepo;
    public Users register(RegisterRequest request) {
        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Users.Role.EMPLOYEE);
        user.setCreatedAt(LocalDateTime.now());
        return usersRepo.save(user);
    }

    public List<Profile> findAll() {
        return profileRepo.findAll();
    }

    public Profile findByEmail(String email) {
        Users user = usersRepo.findByEmail(email);
        return profileRepo.findByUser(user);
    }

    public Profile fillProfile(ProfileRequestDto dto) {

        Users user = usersRepo.findById(dto.getUserId())
                .orElseThrow(()-> new NoSuchElementException("not found"));

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstname(dto.getFirstname());
        profile.setLastname(dto.getLastname());
        profile.setProfession(dto.getProfession());
        profile.setGoodAt(dto.getGoodAt());
        return profileRepo.save(profile);
    }

    public Optional<Profile> myProfile(String username) {
        Users user = usersRepo.findByLogin(username)
                .orElseThrow(()->new NoSuchElementException("not found"));

        return Optional.ofNullable(profileRepo.findByUser(user));
    }

    public Profile findById(Long id) {
        Users user = usersRepo.findById(id)
                .orElseThrow(()->new NoSuchElementException("user not found"));
        return profileRepo.findByUser(user);
    }

    public boolean checkProfile(String login) {
        Users user = usersRepo.findByLogin(login)
                .orElseThrow(()->new NoSuchElementException("profile not found"));
        return profileRepo.existsByUser(user);
    }

    public EmployeeHistory getHistory(Long id) {
        // Fetch user or throw exception if not found
        Users user = usersRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Fetch profile directly associated with the user
        Profile profile = profileRepo.findByUser(user);
        if (profile == null) {
            throw new NoSuchElementException("Profile not found for user");
        }

        // Initialize EmployeeHistory object
        EmployeeHistory history = new EmployeeHistory();

        // Fetch projects where the profile is an employee
        List<Project> projects = projectRepo.findAll().stream()
                .filter(project -> project.getEmployees().contains(profile))
                .toList();

        // (Optional) Calculate rating - add meaningful logic if required
        int rating = calculateRating(projects, profile);

        // Set history fields
        history.setFirstname(profile.getFirstname());
        history.setLastname(profile.getLastname());
        history.setProfession(profile.getProfession());
        history.setRating(rating);
        history.setProjects(projects.stream().map(Project::getName).toList());

        return history;
    }

    // Example helper method for rating calculation
    private int calculateRating(List<Project> projects, Profile profile) {
        List<Task> result = new ArrayList<>();

        for (Project project : projects) {
            List<Task> tasks = taskRepo.findByProjectAndAssignedTo(project, profile);
            result.addAll(tasks);
        }

        return result.size();
    }


}
