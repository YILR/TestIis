package testiis.model;

import java.util.Objects;


/**
 * POJO class
 *
 * @author Ilnur Yakhin
 */
public class Department {


    private String depCode;
    private String depJob;
    private String description;

    public Department(String depCode, String depJob, String description) {
        this.depCode = depCode;
        this.depJob = depJob;
        this.description = description;
    }


    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(depCode, that.depCode) &&
                Objects.equals(depJob, that.depJob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depCode, depJob);
    }

    @Override
    public String toString() {
        return "Departament{" +
                "depCode='" + depCode + '\'' +
                ", depJob='" + depJob + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
