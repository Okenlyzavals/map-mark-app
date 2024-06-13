package by.baranouski.mapphotoapp.markservice.util;

import by.baranouski.mapphotoapp.markservice.controller.CommentController;
import by.baranouski.mapphotoapp.markservice.controller.MarkController;
import by.baranouski.mapphotoapp.markservice.model.Comment;
import by.baranouski.mapphotoapp.markservice.model.Mark;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LinkAssembler {

    public Link getResponseEntity(Mark entity) {
        return linkTo(methodOn(MarkController.class).getMark(entity.getId())).withSelfRel();
    }

    public Link getResponseEntity(Comment entity) {
        return linkTo(methodOn(CommentController.class).getComment(entity.getId())).withSelfRel();
    }
}
