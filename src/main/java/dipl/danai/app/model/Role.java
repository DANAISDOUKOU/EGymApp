package dipl.danai.app.model;

public enum Role {
    ATHLETE("Athlete"),
    GYM("Gym"),
    INSTRUCTOR("Instructor");

    private final String value;

    private Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}