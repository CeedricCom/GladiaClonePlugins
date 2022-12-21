package me.deltaorion.townymissionsv2.storage;

public enum StorageType {
    SQLITE("sqlite","db");

    private final String identifier;
    private String fileExtension;

    StorageType(String identifier) {
        this(identifier,null);
    }

    StorageType(String identifier, String fileExtension) {
        this.identifier = identifier;
        this.fileExtension = fileExtension;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
