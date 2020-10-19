package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;

@NodeEntity(label = "Victim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VictimNode extends Node implements Victim {

    private Long totalNumberOfFatalities;

    private Long numberOfPerpetratorFatalities;

    private Long totalNumberOfInjured;

    private Long numberOfPerpetratorInjured;

    private Long valueOfPropertyDamage;

    @Builder
    public VictimNode(Long id, Long totalNumberOfFatalities, Long numberOfPerpetratorFatalities, Long totalNumberOfInjured,
                      Long numberOfPerpetratorInjured, Long valueOfPropertyDamage) {
        super(id);
        this.totalNumberOfFatalities = totalNumberOfFatalities;
        this.numberOfPerpetratorFatalities = numberOfPerpetratorFatalities;
        this.totalNumberOfInjured = totalNumberOfInjured;
        this.numberOfPerpetratorInjured = numberOfPerpetratorInjured;
        this.valueOfPropertyDamage = valueOfPropertyDamage;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof VictimNode)) return false;

        VictimNode that = (VictimNode) o;
        return Objects.equals(getTotalNumberOfFatalities(), that.getTotalNumberOfFatalities()) &&
                Objects.equals(getNumberOfPerpetratorFatalities(), that.getNumberOfPerpetratorFatalities()) &&
                Objects.equals(getTotalNumberOfInjured(), that.getTotalNumberOfInjured()) &&
                Objects.equals(getNumberOfPerpetratorInjured(), that.getNumberOfPerpetratorInjured()) &&
                Objects.equals(getValueOfPropertyDamage(), that.getValueOfPropertyDamage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotalNumberOfFatalities(), getNumberOfPerpetratorFatalities(),
                getTotalNumberOfInjured(), getNumberOfPerpetratorInjured(), getValueOfPropertyDamage());
    }
}