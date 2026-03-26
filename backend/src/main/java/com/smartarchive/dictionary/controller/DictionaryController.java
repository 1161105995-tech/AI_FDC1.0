package com.smartarchive.dictionary.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.dictionary.dto.DictionaryCategoryCommand;
import com.smartarchive.dictionary.dto.DictionaryCategoryResponse;
import com.smartarchive.dictionary.dto.DictionaryCategoryUpdateCommand;
import com.smartarchive.dictionary.dto.DictionaryItemCommand;
import com.smartarchive.dictionary.dto.DictionaryItemResponse;
import com.smartarchive.dictionary.dto.DictionaryItemUpdateCommand;
import com.smartarchive.dictionary.service.DictionaryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService dictionaryService;

    @GetMapping("/categories")
    public ApiResponse<List<DictionaryCategoryResponse>> listCategories() {
        return ApiResponse.success(dictionaryService.listCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<DictionaryCategoryResponse> createCategory(@Valid @RequestBody DictionaryCategoryCommand command) {
        return ApiResponse.success(dictionaryService.createCategory(command));
    }

    @PutMapping("/categories/{categoryCode}")
    public ApiResponse<DictionaryCategoryResponse> updateCategory(@PathVariable String categoryCode,
                                                                  @Valid @RequestBody DictionaryCategoryUpdateCommand command) {
        return ApiResponse.success(dictionaryService.updateCategory(categoryCode, command));
    }

    @DeleteMapping("/categories/{categoryCode}")
    public ApiResponse<Void> deleteCategory(@PathVariable String categoryCode) {
        dictionaryService.deleteCategory(categoryCode);
        return ApiResponse.success(null);
    }

    @GetMapping("/categories/{categoryCode}/items")
    public ApiResponse<List<DictionaryItemResponse>> listItems(@PathVariable String categoryCode) {
        return ApiResponse.success(dictionaryService.listItems(categoryCode));
    }

    @PostMapping("/categories/{categoryCode}/items")
    public ApiResponse<DictionaryItemResponse> createItem(@PathVariable String categoryCode,
                                                          @Valid @RequestBody DictionaryItemCommand command) {
        return ApiResponse.success(dictionaryService.createItem(categoryCode, command));
    }

    @PutMapping("/categories/{categoryCode}/items/{itemCode}")
    public ApiResponse<DictionaryItemResponse> updateItem(@PathVariable String categoryCode,
                                                          @PathVariable String itemCode,
                                                          @Valid @RequestBody DictionaryItemUpdateCommand command) {
        return ApiResponse.success(dictionaryService.updateItem(categoryCode, itemCode, command));
    }

    @DeleteMapping("/categories/{categoryCode}/items/{itemCode}")
    public ApiResponse<Void> deleteItem(@PathVariable String categoryCode, @PathVariable String itemCode) {
        dictionaryService.deleteItem(categoryCode, itemCode);
        return ApiResponse.success(null);
    }
}