package org.recap.model;

import java.util.List;

/**
 * Created by sudhishk on 15/12/16.
 */
public abstract class AbstractRequestItem {

    private List<String> itemBarcodes;
    private String itemOwningInstitution=""; // PUL, CUL, NYPL

    public List<String> getItemBarcodes() {
        return itemBarcodes;
    }

    public void setItemBarcodes(List<String> itemBarcodes) {
        this.itemBarcodes = itemBarcodes;
    }

    public String getItemOwningInstitution() {
        return itemOwningInstitution;
    }

    public void setItemOwningInstitution(String itemOwningInstitution) {
        this.itemOwningInstitution = itemOwningInstitution;
    }
}
