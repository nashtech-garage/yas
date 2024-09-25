package com.yas.automation.ui.form;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

@Getter
public class ProductForm extends BaseForm {

    @FindBy(how = How.ID, using = "name")
    private WebElement name;

    @FindBy(how = How.ID, using = "slug")
    private WebElement slug;

    @FindBy(how = How.ID, using = "price")
    private WebElement price;

    @FindBy(how = How.ID, using = "sku")
    private WebElement sku;

    @FindBy(how = How.ID, using = "gtin")
    private WebElement gtin;

    @FindBy(how = How.ID, using = "select-option-brandId")
    private WebElement brand;

    @FindBy(how = How.ID, using = "isFeatured")
    private WebElement isFeatured;

    @FindBy(how = How.ID, using = "select-option-taxClassId")
    private WebElement tax;

    @FindBy(how = How.CLASS_NAME, using = "ql-editor")
    private WebElement description;

    @FindBy(id = "shortDescription")
    private WebElement shortDescription;

    @FindBy(id = "react-select-2-input")
    private WebElement productVariationAvailableOption;

    @FindBy(id = "react-select-2-listbox")
    private WebElement productVariationAvailableOptionListBox;

    @FindBy(id = "RAM")
    private WebElement ram;

    @FindBy(how = How.NAME, using = "optionPrice")
    private WebElement ramOptionPrice;

    @FindBy(how = How.NAME, using = "optionGtin")
    private WebElement ramOptionGtin;

    @FindBy(how = How.NAME, using = "optionSku")
    private WebElement ramOptionSku;

    @FindBy(id = "sub-thumbnail-0")
    private WebElement ramThumbnail;

    @FindBy(id = "sub-images-0")
    private WebElement productSubImage;

    @FindBy(xpath = "//button[@type='submit' and contains(text(),'Create')]")
    private WebElement createBtn;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Product Attributes')]")
    private WebElement productAttributeNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Category Mapping')]")
    private WebElement categoryMappingNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Cross-sell Product')]")
    private WebElement crossSellProductNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'SEO')]")
    private WebElement seoNav;

    @FindBy(id = "metaTitle")
    private WebElement seoMetaTitle;

    @FindBy(id = "metaKeyword")
    private WebElement seoMetaKeyword;

    @FindBy(id = "metaDescription")
    private WebElement seoMetaDescription;

    @FindBy(xpath = "//button[contains(text(),'Manage Cross-Sell Product')]")
    private WebElement managedCrossSellProductBtn;

    @FindBy(xpath = "//button[contains(text(),'Apply')]")
    private WebElement productTemplateApplyButton;

    @FindBy(id = "product-template")
    private WebElement productTemplate;

    @FindBy(id = "attribute")
    private WebElement productTemplateAvailableAttribute;

    @FindBy(id = "iphone-15-pro-max")
    private WebElement ip15ChkBox;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Product Images')]")
    private WebElement productImgNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Product Variations')]")
    private WebElement productVariantsNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Related Products')]")
    private WebElement relatedProductNav;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Add Related Product')]")
    private WebElement relatedProductAddBtn;

    @FindBy(xpath = "//button[@type='button' and contains(text(),'Close')]")
    private WebElement modalCloseBtn;

    @FindBy(xpath = "//button[contains(text(),'Add Option')]")
    private WebElement addOptionBtn;

    @FindBy(xpath = "//button[contains(text(),'Create')]")
    private WebElement productAttributeCreateBtn;

    @FindBy(xpath = "//button[contains(text(),'Generate Combine')]")
    private WebElement generateCombinationBtn;

    @FindBy(how = How.ID, using = "main-thumbnail")
    private WebElement thumbnail;

    @FindBy(how = How.ID, using = "main-product-images")
    private WebElement image;

    @FindBy(id = "checkbox-5")
    private WebElement catMappingLaptopChkBox;

    public ProductForm(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    @Override
    public WebElement getSubmitBtn() {
        return createBtn;
    }
}
