package by.baranouski.mapphotoapp.userservice.util;

import by.baranouski.mapphotoapp.userservice.controller.UserController;
import by.baranouski.mapphotoapp.userservice.model.User;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LinkAssembler {

    public Link getResponseEntity(User entity) {
        return linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel();
    }

}
