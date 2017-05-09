package com.salexandru.xcore.preferencepage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtraBinding {
	
	private final String entity_;
	private final List<IBinding> bindings_ = new ArrayList<>();
	
	public static ExtraBinding parse(String str) {
		String[] tmp = str.split(",");
		List<IBinding> bindings = new ArrayList<>();
		
		for (int i = 1; i < tmp.length; ++i) {
			if (!tmp[i].contains(":")) {
				bindings.add(new SimpleBinding(tmp[i].trim()));
			}
			else {
				String[] types = tmp[i].split(":");
				bindings.add(new ExtraTypeBinding(types[0], types[1]));
			}
		}
		
		return new ExtraBinding(tmp[0], bindings);
	}
	
	public static ExtraBinding parse(String entity, String str) {
		String[] tmp = str.split(",");
		List<IBinding> bindings = new ArrayList<>();
		
		for (int i = 0; i < tmp.length; ++i) {
			if (!tmp[i].contains(":")) {
				bindings.add(new SimpleBinding(tmp[i].trim()));
			}
			else {
				String[] types = tmp[i].split(":");
				bindings.add(new ExtraTypeBinding(types[0], types[1]));
			}
		}
		
		return new ExtraBinding(entity, bindings);
	}
	
	public ExtraBinding (String entity, List<IBinding> bindings) {
		if (null == entity || entity.trim().isEmpty() || null == bindings) {
			throw new IllegalArgumentException("Entity / bindings are null or empty");
		}
		
		entity_ = entity;
		
		for (IBinding binding: bindings) {
			if (null != binding) {
				bindings_.add(binding);
			}
		}
	}
	
	public ExtraBinding (String entity) {
		if (null == entity || entity.trim().isEmpty()) {
			throw new IllegalArgumentException("Entity / bindings are null or empty");
		}
		
		entity_ = entity;
	}
	
	public ExtraBinding addBinding(IBinding b) { 
		bindings_.add(b);
		return this;
	}
	
	public ExtraBinding addBindings(IBinding... bindings) {
		if (null != bindings) {
			for (IBinding b: bindings) {
				if (null != b) {
					bindings_.add(b);
				}
			}
		}
		return this;
	}
	
	public ExtraBinding addBindings(List<IBinding> bindings) {
		if (null != bindings) {
			for (IBinding b: bindings) {
				if (null != b) {
					bindings_.add(b);
				}
			}
		}
		return this;
	}
	
	public ExtraBinding replaceBindingAt(int idx, IBinding b) {
		if (idx < 0 || idx >= bindings_.size()) {
			throw new IllegalArgumentException("idx is invalid");
		}
		bindings_.set(idx, b);
		
		return this;
	}
	
	public String getEntity() { return entity_; }
	public List<IBinding> getBindings() { return Collections.unmodifiableList(bindings_); }
	
	public String getBindingsAsString() {
		String[] s = new String[bindings_.size()];
		
		for (int i = 0; i < bindings_.size(); ++i) {
			s[i] = bindings_.get(i).toString();
		}
		
		return String.join(",", s);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String[] s = new String[bindings_.size()];
		
		for (int i = 0; i < bindings_.size(); ++i) {
			s[i] = bindings_.get(i).toString();
		}
		
		buffer.append(entity_ + ",");
		buffer.append(String.join(",", s));
		
		return buffer.toString();
	}
}
