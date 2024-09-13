package com.hfad.cs426_final_project.DataStorage;

import java.util.HashMap;
import java.util.Map;

public class LandState {
    private Map<String, TileInfo> tiles;

    public LandState() {
        tiles = new HashMap<>();
    }

    public Map<String, TileInfo> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, TileInfo> tiles) {
        this.tiles = tiles;
    }

    public static class TileInfo {
        private String type; // "NORMAL" or "PLUS"
        private Integer blockId; // null for PLUS tiles

        public TileInfo() {}

        public TileInfo(String type, Integer blockId) {
            this.type = type;
            this.blockId = blockId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getBlockId() {
            return blockId;
        }

        public void setBlockId(Integer blockId) {
            this.blockId = blockId;
        }
    }
}