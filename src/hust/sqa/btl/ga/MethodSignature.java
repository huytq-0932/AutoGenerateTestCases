package hust.sqa.btl.ga;

import java.util.List;

/**
 * Quản lý chữ kí method/constructor
 */
public class MethodSignature {
    /**
     * Tên method
     */
    private String name;

    /**
     * List parameter
     */
    private List<String> parameters;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the parameters
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param name
     * @param parameters
     */
    public MethodSignature(String name, List<String> parameters) {
        super();
        this.name = name;
        this.parameters = parameters;
    }

}
