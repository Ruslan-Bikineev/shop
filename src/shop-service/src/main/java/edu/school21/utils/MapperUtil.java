package edu.school21.utils;

import api.edu.school21.proto.grpc.v1.AuthentificationUserRqDto;
import api.edu.school21.proto.grpc.v1.ChangeUserPasswordRqDto;
import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import api.edu.school21.proto.grpc.v1.JwtTokenRsDto;
import api.edu.school21.proto.grpc.v1.PasswordRecoveryUserRqDto;
import edu.school21.dto.ClientDto;
import edu.school21.dto.ProductDto;
import edu.school21.dto.SupplierDto;
import edu.school21.dto.request.UserAuthRqDto;
import edu.school21.dto.request.UserChangePasswordRqDto;
import edu.school21.dto.request.UserPasswordRecoveryRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.entity.Category;
import edu.school21.entity.Client;
import edu.school21.entity.Image;
import edu.school21.entity.Product;
import edu.school21.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MapperUtil {

    ClientDto mapToClientDto(Client source);

    Client mapToClient(ClientDto source);

    SupplierDto mapToSupplierDto(Supplier source);

    @Mapping(target = "products", ignore = true)
    Supplier mapToSupplier(SupplierDto source);

    @Mapping(target = "image", expression = "java(mapToImage(source.getImageId()))")
    @Mapping(target = "supplier", expression = "java(mapToSupplier(source.getSupplierId()))")
    @Mapping(target = "category", expression = "java(mapToCategory(source.getCategory()))")
    Product mapToProduct(ProductDto source);

    @Mapping(target = "imageId", source = "image.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "category", source = "category.name")
    ProductDto mapToProductDto(Product source);

    @Mapping(target = "id", ignore = true)
    Category mapToCategory(String name);

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "product", ignore = true)
    Image mapToImage(UUID id);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "products", ignore = true)
    Supplier mapToSupplier(UUID id);

    @Mapping(target = "accessToken", source = "token")
    TokenRsDto mapToTokenRsDto(JwtTokenRsDto source);

    default CreateUserRqDto mapToCreateUserRqDto(UserRqDto source) {
        return CreateUserRqDto.newBuilder()
                .setFirstName(source.getFirstName())
                .setLastName(source.getLastName())
                .setPassword(source.getPassword())
                .setMail(source.getMail())
                .setPhoneNumber(source.getPhoneNumber())
                .build();
    }

    default AuthentificationUserRqDto mapToAuthentificationUserRqDto(UserAuthRqDto source) {
        return AuthentificationUserRqDto.newBuilder()
                .setPassword(source.getPassword())
                .setMail(source.getMail())
                .build();
    }

    default ChangeUserPasswordRqDto mapToChangeUserPasswordRqDto(UserChangePasswordRqDto source) {
        return ChangeUserPasswordRqDto.newBuilder()
                .setNewPassword(source.getNewPassword())
                .build();
    }

    default PasswordRecoveryUserRqDto mapToPasswordRecoveryUserRqDto(UserPasswordRecoveryRqDto source) {
        return PasswordRecoveryUserRqDto.newBuilder()
                .setMail(source.getMail())
                .build();
    }
}
