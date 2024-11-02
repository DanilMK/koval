package net.smok.koval.forging;

import net.minecraft.util.Identifier;
import net.smok.koval.Properties;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

public interface ConditionFactory {

    ConditionGroup.Condition build(Object value) throws InvalidTargetObjectTypeException;

    ConditionGroup.Condition build(Identifier identifier, Properties properties) throws InvalidTargetObjectTypeException;

}
