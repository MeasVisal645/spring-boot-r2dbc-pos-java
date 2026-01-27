package backend.Entities;

import lombok.Getter;

@Getter
public enum Unit {
    PCS("PCS"),
    SET("SET"),
    CASE("CASE"),
    KG("KG"),
    BOTTLE("BOTTLE"),
    BOX("BOX");

    public final String value;

    Unit(String value) {
        this.value = value;
    }
}
