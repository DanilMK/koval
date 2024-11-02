package net.smok.koval;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.Debug;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Assembly {


    public static final Assembly EMPTY = new Assembly(new HashMap<>(), new HashMap<>());

    // Static

    @Contract("_ -> new")
    public static @NotNull Assembly fromItemsMap(@NotNull Map<Vec2Int, Identifier> items) {
        Map<Vec2Int, net.smok.koval.Part> parts = items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> KovalRegistry.PARTS.get(entry.getValue())));
        return new Assembly(parts, assemble(parts));
    }

    @Contract("_ -> new")
    public static @NotNull Assembly fromPartsMap(@NotNull Map<Vec2Int, net.smok.koval.Part> parts) {
        return new Assembly(parts, assemble(parts));
    }

    @Contract("_ -> new")
    public static @NotNull Assembly fromNbt(@NotNull NbtCompound nbt) {
        Map<Vec2Int, net.smok.koval.Part> parts = nbtToPartsMap(nbt);
        return new Assembly(parts, assemble(parts));
    }

    @Contract("_ -> new")
    public static @NotNull Map<Vec2Int, Identifier> nbtToItemsMap(@NotNull NbtCompound nbt) {
        return nbt.getKeys().stream().collect(Collectors.toMap(Vec2Int::fromString, string -> new Identifier(nbt.getString(string))));
    }

    @Contract("_ -> new")
    public static @NotNull Map<Vec2Int, net.smok.koval.Part> nbtToPartsMap(@NotNull NbtCompound nbt) {
        return nbt.getKeys().stream().collect(Collectors.toMap(Vec2Int::fromString, string -> KovalRegistry.PARTS.get(new Identifier(nbt.getString(string)))));
    }

    public static @NotNull NbtCompound itemsMapToNbt(@NotNull Map<Vec2Int, Identifier> map) {
        NbtCompound result = new NbtCompound();
        for (Map.Entry<Vec2Int, Identifier> entry : map.entrySet()) {
            result.put(entry.getKey().toString(), NbtString.of(entry.getValue().toString()));
        }
        return result;
    }

    private static HashMap<Identifier, PartRecord> assemble(Map<Vec2Int, net.smok.koval.Part> parts) {
        HashMap<Identifier, ArrayList<Vec2Int>> rawMap = new HashMap<>();

        // Fill raw map
        for (Map.Entry<Vec2Int, net.smok.koval.Part> entry : parts.entrySet()) {
            Vec2Int pos = entry.getKey();
            net.smok.koval.Part part = entry.getValue();

            for (Map.Entry<Identifier, AbstractParameter> parameterEntry : part.parameters().parameters().entrySet()) {
                Identifier id = parameterEntry.getKey();

                ArrayList<Vec2Int> resultList = rawMap.getOrDefault(id, new ArrayList<>());
                collectByPath(parts, resultList, pos, id);


                if (!rawMap.containsKey(id)) rawMap.put(id, resultList);
            }
        }


        // Collapse rawMap
        HashMap<Identifier, PartRecord> resultMap = new HashMap<>();
        for (Map.Entry<Identifier, ArrayList<Vec2Int>> entry : rawMap.entrySet()) {
            Identifier id = entry.getKey();
            Vec2Int pos = entry.getValue().get(entry.getValue().size() - 1);
            net.smok.koval.Part part = parts.get(pos);
            AbstractParameter parameter = part.parameters().get(id);

            resultMap.put(id, new PartRecord(pos, part, parameter));
        }


        return resultMap;
    }

    private static void collectByPath(Map<Vec2Int, net.smok.koval.Part> parts, ArrayList<Vec2Int> result, Vec2Int start, Identifier id) {
        ArrayList<Vec2Int> addList = new ArrayList<>();
        Stack<Vec2Int> addStack = new Stack<>();
        addStack.push(start);

        // Get last element as default insert point
        int insertPoint = result.size() - 1;

        // Simple pathfinder
        while (!addStack.empty()) {
            Vec2Int next = addStack.pop();
            if (addList.contains(next)) continue;

            { // If next position already contains in result list then we add new list right after next record.
                int index = result.indexOf(next);
                insertPoint = Math.max(index, insertPoint);
                if (index >= 0) continue;
            }

            addList.add(0, next);

            // Add all new pointers
            AbstractParameter parameter = parts.get(next).parameters().get(id);
            if (parameter == null) {
                Debug.err(MessageFormat.format("(Assembly) Part ({0}) doesn''t contain parameter with id ({1})", parts.get(next), id));
                continue;
            }
            for (Vec2Int pointer : parameter.getPointers()) {

                addStack.push(next.add(pointer));
            }
        }

        if (!addList.isEmpty()) result.addAll(insertPoint + 1, addList);
    }


    // Nonstatic

    private final Map<Identifier, PartRecord> functions;
    private final Map<Vec2Int, net.smok.koval.Part> parts;


    private Assembly(Map<Vec2Int, net.smok.koval.Part> parts, Map<Identifier, PartRecord> functions) {
        this.parts = parts;
        this.functions = functions;
    }




    public int applyValue(Identifier valueId, int start, Object... params) {
        return applyObjectValue(valueId, params) instanceof Number number ? (int)number.floatValue() : start;
    }

    public float applyValue(Identifier valueId, float start, Object... params) {
        return applyObjectValue(valueId, params) instanceof Number number ? number.floatValue() : start;
    }

    public boolean applyValue(Identifier valueId, boolean start, Object... params) {
        return applyObjectValue(valueId, params) instanceof Boolean b ? b : start;
    }

    public Object applyObjectValue(Identifier identifier, Object... actionParams) {
        PartRecord record = functions.get(identifier);

        return record == null ? null : record.parameter().get(record.getContext(parts, actionParams));
    }


    public void appendTooltip(List<Text> tooltip) {
        functions.forEach((identifier, partRecord) ->
                tooltip.add(Text.translatable(identifier.toTranslationKey("koval.parameter"))
                .append(": ")
                .append(partRecord.parameter.toText(partRecord.getContext(parts, new Object[0])))
                .styled(style -> style.withColor(partRecord.part.material().color()))
                ));
    }

    private record PartRecord(Vec2Int pos, Part part, AbstractParameter parameter) {

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            PartRecord record = (PartRecord) object;
            return Objects.equals(pos, record.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos);
        }

        public ActionContext getContext(Map<Vec2Int, Part> parts, Object[] actionParams) {
            return new ActionContext(parts::get, part, actionParams, pos);
        }
    }
}
