package com.flowable.training.dp.t22.symbols;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SymbolsResource {

    public final Set<Symbol> symbols = new HashSet<>();

    public SymbolsResource() {
        symbols.add(new Symbol("MSFT"));
        symbols.add(new Symbol("GOOG"));
        symbols.add(new Symbol("TSLA"));
        symbols.add(new Symbol("FB"));
        symbols.add(new Symbol("AMZN"));
        symbols.add(new Symbol("AAPL"));
        symbols.add(new Symbol("NFLX"));
    }

    @GetMapping(value = "/symbols", produces = "application/json")
    public Set<Symbol> symbols() {
        return symbols;
    }

    @GetMapping(value = "/symbols/{id}", produces = "application/json")
    public Symbol symbols(@PathVariable("id") String id) {
        return symbols.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public static final class Symbol {

        private String id;

        public Symbol(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
