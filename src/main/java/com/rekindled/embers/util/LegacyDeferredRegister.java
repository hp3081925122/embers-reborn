package com.rekindled.embers.util;

import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LegacyDeferredRegister<T> {

	DeferredRegister<T> current;
	DeferredRegister<T> old;

	public LegacyDeferredRegister(DeferredRegister<T> current, DeferredRegister<T> old) {
		this.current = current;
		this.old = old;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <I extends T> DeferredHolder<T, I> register(final String name, final Supplier<? extends I> sup) {
		DeferredHolder<T, I> reg = (DeferredHolder<T, I>) (DeferredHolder) this.current.register(name, sup);
		this.old.register(name, sup);
		return reg;
	}
}
