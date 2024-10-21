package com.yas.automation.ui.pages;

import com.yas.automation.ui.constants.CategoryConstants;
import com.yas.automation.ui.form.CategoryForm;
import com.yas.automation.base.form.InputType;
import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.base.page.BasePage;
import com.yas.automation.base.service.InputDelegateService;
import com.yas.automation.base.util.WebElementUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class NewCategoryPage extends BasePage {

    private final WebDriverFactory webDriverFactory;

    private final InputDelegateService inputDelegateService;

    private final CategoryForm categoryForm;

    @Getter
    private String createdName = "";

    public NewCategoryPage(WebDriverFactory webDriverFactory,
                           InputDelegateService inputDelegateService) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
        this.inputDelegateService = inputDelegateService;
        categoryForm = new CategoryForm(webDriverFactory.getChromeDriver());
    }

    public void fillInAllNecessaryField() {
        createdName = WebElementUtil.createFieldText(
                CategoryConstants.CATEGORY_NAME);
        inputDelegateService.setInputValue(InputType.TEXT, categoryForm.getName(), createdName);
        inputDelegateService.setInputValue(InputType.TEXT, categoryForm.getSlug(), WebElementUtil.createFieldText(
                CategoryConstants.CATEGORY_SLUG));
        inputDelegateService.setInputValue(InputType.TEXT, categoryForm.getDescription(), WebElementUtil.createFieldText(
                CategoryConstants.CATEGORY_DESCRIPTION));
        inputDelegateService.setInputValue(InputType.TEXT, categoryForm.getMetaKeywords(), WebElementUtil.createFieldText(
                CategoryConstants.CATEGORY_METAKEYWORDS));
        this.scrollDown();
        inputDelegateService.setInputValue(InputType.TEXT, categoryForm.getMetaDescription(), WebElementUtil.createFieldText(
                CategoryConstants.CATEGORY_METADESCRIPTION));
        categoryForm.getIsPublish().click();
        inputDelegateService.setInputValue(InputType.FILE, categoryForm.getCategoryImage(), "sampledata/images/category.png");

    }

    public void clickSaveButton() {
        categoryForm.getSubmitBtn().click();
    }

}
