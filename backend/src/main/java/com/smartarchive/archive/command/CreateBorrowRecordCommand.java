package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBorrowRecordCommand {
    @NotBlank
    private String archiveCode;
    @NotBlank
    private String archiveTitle;
    @NotBlank
    private String borrower;
    @NotBlank
    private String borrowType;
    @NotNull
    private LocalDate expectedReturnDate;
}