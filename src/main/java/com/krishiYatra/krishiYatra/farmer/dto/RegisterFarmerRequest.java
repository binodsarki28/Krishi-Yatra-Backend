package com.krishiYatra.krishiYatra.farmer.dto;

import com.krishiYatra.krishiYatra.common.enums.FarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Request object for registering a user as a farmer")
public class RegisterFarmerRequest {
    
    @NotEmpty(message = "Farm types must not be empty")
    @Schema(description = "List of farm categories", example = "[\"CROP\", \"VEGETABLE\"]")
    private List<FarmType> types;

    @NotBlank(message = "Farm name is required")
    @Schema(description = "Display name of the farm", example = "Green Valley Farm")
    private String farmName;

    @NotBlank(message = "Farm location is required")
    @Schema(description = "Physical address or coordinates of the farm", example = "Kathmandu, Nepal")
    private String farmLocation;

    @NotNull(message = "Farm area is required")
    @Positive(message = "Farm area must be greater than zero")
    @Schema(description = "Size of the farm in hectares", example = "2.5")
    private Double farmArea;
}
