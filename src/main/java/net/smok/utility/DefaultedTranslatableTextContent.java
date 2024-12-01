package net.smok.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DefaultedTranslatableTextContent implements TextContent {
    private final String deafultString;
    private final TranslatableTextContent translatableTextContent;

    private DefaultedTranslatableTextContent(TranslatableTextContent translatableTextContent, String deafultString) {
        this.deafultString = deafultString;
        this.translatableTextContent = translatableTextContent;
    }

    public DefaultedTranslatableTextContent(String key, String deafultString) {
        this.translatableTextContent = new TranslatableTextContent(key);
        this.deafultString = deafultString;
    }

    public DefaultedTranslatableTextContent(String key, String deafultString, Object... args) {
        this.translatableTextContent = new TranslatableTextContent(key, args);
        this.deafultString = deafultString;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        //return translatableTextContent.visit(visitor).or(() -> StringVisitable.plain(deafultString).visit(visitor));
        if (Language.getInstance().get(translatableTextContent.getKey()).equals(translatableTextContent.getKey()))
            return StringVisitable.plain(deafultString).visit(visitor);
        return translatableTextContent.visit(visitor);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        //return translatableTextContent.visit(visitor, style).or(() -> StringVisitable.plain(deafultString).visit(visitor, style));
        if (Language.getInstance().get(translatableTextContent.getKey()).equals(translatableTextContent.getKey()))
            return StringVisitable.plain(deafultString).visit(visitor, style);
        return translatableTextContent.visit(visitor, style);
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        return MutableText.of(new DefaultedTranslatableTextContent((TranslatableTextContent) translatableTextContent.parse(source, sender, depth).getContent(), deafultString));
    }
}
