package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateInventoryTaskCommand {
    @NotBlank
    private String warehouseCode;
    @NotBlank
    private String inventoryScope;
    @NotBlank
    private String owner;
    @NotNull
    private LocalDate dueDate;
}