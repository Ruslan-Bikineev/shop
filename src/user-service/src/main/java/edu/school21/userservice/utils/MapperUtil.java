package edu.school21.userservice.utils;

import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import edu.school21.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperUtil {

    default User mapToUser(CreateUserRqDto source) {
        return User.builder()
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .password(source.getPassword())
                .mail(source.getMail())
                .phoneNumber(source.getPhoneNumber())
                .build();
    }
}
