package com.architecture.admin.libraries;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.zip.ZipEntry;

@Component
public class SortLibrary {

    // mac에서 압축 해제시 __MACOSX폴더 생성됨
    // 파일의 확장 정보를 기록하는 리소스 포크가 저장되는 폴더
    private final String MAC_OS_X = "__MACOSX";


    /**
     * zip 파일 sort
     * @param entries
     * @return
     */
    public List<ZipArchiveEntry> entrySort(Enumeration<? extends ZipArchiveEntry> entries) {

        List<EntryMap> mapList = new ArrayList<>();

        // zip 파일 리스트 목록 순환
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();

            if (entry.getName().startsWith(MAC_OS_X)) {
                continue;
            }

            String sReplace = entry.getName().replaceAll("[^\\d]", ",");
            String[] sSplit = sReplace.split(",");

            StringBuilder stringBuilder = new StringBuilder();

            for (String s : sSplit) {
                if (!s.isEmpty()) {
                    stringBuilder.append(String.format("%04d", Long.parseLong(s)));
                }
            }

            mapList.add(new EntryMap(stringBuilder.toString(), entry));
        }

        Collections.sort(mapList);

        List<ZipArchiveEntry> zipEntryList = new ArrayList<>();
        for (EntryMap entryMap : mapList) {
            zipEntryList.add(entryMap.entry);
        }

        return zipEntryList;
    }


    class EntryMap implements Comparable<EntryMap> {
        private String key;
        private ZipArchiveEntry entry;

        public EntryMap(String key, ZipArchiveEntry entry) {
            this.key = key;
            this.entry = entry;
        }

        @Override
        public int compareTo(EntryMap entryMap) {
            if (entryMap.key.compareToIgnoreCase(key) < 0) {
                return 1;
            } else if (entryMap.key.compareToIgnoreCase(key) > 0) {
                return -1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[ " + this.key + ": " + this.entry + " ]";
        }
    }

}
