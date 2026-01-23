package backend.Entities;

import lombok.Getter;

@Getter
public enum Unit {
    PCS("pcs"),
    SET("set"),
    CASE("case"),
    KG("kg"),
    BOTTLE("bottle"),
    BOX("box");

    public final String value;

    Unit(String value) {
        this.value = value;
    }
}
