package id.ruriazz.pagination.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    private Integer age;

    private String email;

    private String phone;

    @JsonProperty("birthDate")
    private String birthDate;

    private String image;

    private String bloodGroup;

    private Integer height;

    private Double weight;

    private String eyeColor;

    @JsonProperty("hair")
    private Hair hair;

    private Address address;

    private Company company;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hair {
        private String color;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String address;
        private String city;
        private String state;
        private String stateCode;
        private String postalCode;
        private Coordinates coordinates;
        private String country;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Coordinates {
            private Double lat;
            private Double lng;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Company {
        private String department;
        private String name;
        private String title;
        private Address address;
    }
}