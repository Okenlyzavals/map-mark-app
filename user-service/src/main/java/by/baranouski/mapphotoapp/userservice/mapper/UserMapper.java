package by.baranouski.mapphotoapp.userservice.mapper;

import _by.baranouski.mapphotoapp.api.model.RegistrationRequestDto;
import _by.baranouski.mapphotoapp.api.model.UserDto;
import _by.baranouski.mapphotoapp.api.model.UserRoleDto;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User toUser(RegistrationRequestDto registrationRequestDto);

    UserDto toDto(User user);

    Role toRole(UserRoleDto roleDto);
}
