package org.jini.projects.neon.web.sample;

import java.util.Map;

import org.jini.projects.neon.annotations.Render;
import org.jini.projects.neon.collaboration.Collaborative;

public interface SimpleWeb extends Collaborative{
        @Render
        public void update(Map options);
}
