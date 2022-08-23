package com.ayla.hotelsaas.bean;

import java.util.List;

public class NewGroupAbility {
    private String abilityCode;
    private int abilityType;
    private int abilityValueType;
    private List<AbilityValues> abilityValues;
    private String description;
    private String displayName;
    private String version;

    public void setAbilityCode(String abilityCode) {
        this.abilityCode = abilityCode;
    }

    public String getAbilityCode() {
        return abilityCode;
    }

    public void setAbilityType(int abilityType) {
        this.abilityType = abilityType;
    }

    public int getAbilityType() {
        return abilityType;
    }

    public void setAbilityValueType(int abilityValueType) {
        this.abilityValueType = abilityValueType;
    }

    public int getAbilityValueType() {
        return abilityValueType;
    }

    public void setAbilityValues(List<AbilityValues> abilityValues) {
        this.abilityValues = abilityValues;
    }

    public List<AbilityValues> getAbilityValues() {
        return abilityValues;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static class Setup {

        private String max;
        private String min;
        private String step;
        private String unit;
        private String unitName;

        public void setMax(String max) {
            this.max = max;
        }

        public String getMax() {
            return max;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getMin() {
            return min;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public String getStep() {
            return step;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getUnitName() {
            return unitName;
        }

    }

    public static class AbilityValues {

        private String abilitySubCode;
        private int dataType;
        private String displayName;
        private Setup setup;
        private String value;

        public void setAbilitySubCode(String abilitySubCode) {
            this.abilitySubCode = abilitySubCode;
        }

        public String getAbilitySubCode() {
            return abilitySubCode;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setSetup(Setup setup) {
            this.setup = setup;
        }

        public Setup getSetup() {
            return setup;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}



