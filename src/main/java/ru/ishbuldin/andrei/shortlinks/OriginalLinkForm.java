package ru.ishbuldin.andrei.shortlinks;


import java.util.Objects;

public class OriginalLinkForm {

    private String original;

    OriginalLinkForm() {};

    OriginalLinkForm(String original) {
        this.original = original;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OriginalLinkForm)) return false;
        OriginalLinkForm that = (OriginalLinkForm) o;
        return Objects.equals(original, that.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }

    @Override
    public String toString() {
        return "OriginalLinkForm{" +
                "original='" + original + '\'' +
                '}';
    }
}