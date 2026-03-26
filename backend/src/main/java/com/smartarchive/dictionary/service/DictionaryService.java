package com.smartarchive.dictionary.service;

import com.smartarchive.dictionary.dto.DictionaryCategoryCommand;
import com.smartarchive.dictionary.dto.DictionaryCategoryResponse;
import com.smartarchive.dictionary.dto.DictionaryCategoryUpdateCommand;
import com.smartarchive.dictionary.dto.DictionaryItemCommand;
import com.smartarchive.dictionary.dto.DictionaryItemResponse;
import com.smartarchive.dictionary.dto.DictionaryItemUpdateCommand;
import java.util.List;

public interface DictionaryService {
    List<DictionaryCategoryResponse> listCategories();
    List<DictionaryItemResponse> listItems(String categoryCode);
    DictionaryCategoryResponse createCategory(DictionaryCategoryCommand command);
    DictionaryCategoryResponse updateCategory(String categoryCode, DictionaryCategoryUpdateCommand command);
    void deleteCategory(String categoryCode);
    DictionaryItemResponse createItem(String categoryCode, DictionaryItemCommand command);
    DictionaryItemResponse updateItem(String categoryCode, String itemCode, DictionaryItemUpdateCommand command);
    void deleteItem(String categoryCode, String itemCode);
}