package com.whiteboardmaster.WhiteBoardMaster.Controllers;


import com.whiteboardmaster.WhiteBoardMaster.Models.ApplicationUser;
import com.whiteboardmaster.WhiteBoardMaster.Models.ApplicationUserRepository;
import com.whiteboardmaster.WhiteBoardMaster.Models.Board;
import com.whiteboardmaster.WhiteBoardMaster.Models.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class ApplicationUserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationUserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    /*
                        USER ROUTES
     */
    @GetMapping("/login")
    public String showLoginForm(Principal p, Model m){

        this.addUserNameToPage(p, m);

        return "login";
    }

    @GetMapping("/profile")
    public String getMyPage(Principal p, Model m){

        if (p != null) {
            // add user details to page
            ApplicationUser user = userRepository.findByUserName(p.getName());
            m.addAttribute("username", user.getUserName());
            m.addAttribute("firstName", user.getFirstName());
            m.addAttribute("lastName", user.getLastName());

            List<Board> boards = user.getBoards();
            m.addAttribute("boards", boards);
        }
        return "profile";
    }

    @PostMapping("/user/register")
    public RedirectView register(HttpServletRequest request, String username, String password, String firstName, String lastName) {

        // checks for unique username
        if (userRepository.findByUserName(username.toLowerCase()) != null) {
            return new RedirectView("/login");
        }

        // create user and add to database
        ApplicationUser newUser = new ApplicationUser(username.toLowerCase(), passwordEncoder.encode(password), firstName, lastName);
        userRepository.save(newUser);

        // auto-login feature after creating account
        try {
            request.login(username.toLowerCase(), password);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return new RedirectView("/");
    }

    private void addUserNameToPage(Principal p, Model m) {
        if (p != null) {
            m.addAttribute("username", p.getName());
        } else {
            m.addAttribute("username", "New user");
        }
    }

}
