/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class FileReference extends FilePathInfo {
    private static final Logger logger = Logger.getLogger(FileReference.class);

    public interface ProjectFileResolver {
        VirtualFile getVirtualFile(@NotNull String sourcePath, @NotNull Project project);
        PsiFile getPsiFile(@NotNull String sourcePath, @NotNull Project project);
    }

    public static ProjectFileResolver projectFileResolver = null;

    protected final Project project;

    public FileReference(@NotNull String filePath) {
        super(filePath);
        this.project = null;
    }

    public FileReference(@NotNull String filePath, Project project) {
        super(filePath);
        this.project = project;
    }

    public FileReference(@NotNull VirtualFile file, Project project) {
        super(file.getPath());
        this.project = project;
    }

    public FileReference(@NotNull PsiFile file) {
        super(file.getVirtualFile().getPath());
        this.project = file.getProject();
    }

    public FileReference(@NotNull FileReference other) {
        super(other);
        this.project = other.project;
    }

    public Project getProject() {
        return project;
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        return FileReference.getVirtualFile(getFilePath(), project);
    }

    @Nullable
    public VirtualFile getVirtualFileWithAnchor() {
        return FileReference.getVirtualFile(getFilePathWithAnchor(), project);
    }

    @Nullable
    public VirtualFile getVirtualParent() {
        return FileReference.getVirtualFile(getPath(), project);
    }

    @Nullable
    public PsiFile getPsiFile() {
        return FileReference.getPsiFile(getFilePath(), project);
    }

    @Nullable
    public PsiFile getPsiFileWithAnchor() {
        return FileReference.getPsiFile(getFilePathWithAnchor(), project);
    }

    @Nullable
    public MultiMarkdownFile getMultiMarkdownFile() {
        PsiFile file;
        return (file = FileReference.getPsiFile(getFilePath(), project)) instanceof MultiMarkdownFile ?
                (MultiMarkdownFile) file : null;
    }

    @Nullable
    public MultiMarkdownFile getMultiMarkdownFileWithAnchor() {
        PsiFile file;
        return (file = FileReference.getPsiFile(getFilePathWithAnchor(), project)) instanceof MultiMarkdownFile ?
                (MultiMarkdownFile) file : null;
    }

    @Nullable
    public static VirtualFile getVirtualFile(@NotNull String sourcePath, @NotNull Project project) {
        return projectFileResolver == null ? null : projectFileResolver.getVirtualFile(sourcePath, project);
    }

    @Nullable
    public static PsiFile getPsiFile(@NotNull String sourcePath, @NotNull Project project) {
        return projectFileResolver == null ? null : projectFileResolver.getPsiFile(sourcePath, project);
    }

    @Override
    public int compareTo(FilePathInfo o) {
        return !(o instanceof FileReference) || project == ((FileReference) o).project ? super.compareTo(o) : -1;
    }

    @Override
    public String toString() {
        return "FileReference(" +
                innerString() +
                ")";
    }

    @Override
    public String innerString() {
        return super.innerString() +
                "project = '" + (project == null ? "null" : project.getName()) + "', " +
                "";
    }

    public boolean canRenameFileTo(@NotNull final String newName) {
        if (project != null) {
            if (equivalent(false, false, getFileName(), newName)) return true;

            // not just changing file name case
            final VirtualFile virtualFile = getVirtualFile();
            final VirtualFile parent = virtualFile != null ? virtualFile.getParent() : null;
            if (parent != null) {
                if (parent.findChild(newName) == null) {
                    return true;
                    //final boolean[] result = new boolean[1];
                    //
                    //ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    //    @Override
                    //    public void run() {
                    //        try {
                    //            VirtualFile newVirtualFile = parent.createChildData(this, newName);
                    //            result[0] = true;
                    //            try {
                    //                newVirtualFile.delete(this);
                    //            } catch (IOException ignore) {
                    //                logger.info("IOException on delete " + newName, ignore);
                    //            }
                    //        } catch (IOException ignore) {
                    //            // can't create it, so we remove it
                    //            logger.info("IOException on create " + newName, ignore);
                    //        }
                    //    }
                    //});
                    //
                    //return result[0];
                }
            }
        }
        return false;
    }

    public boolean canCreateFile() {
        final String newName = getFileName();
        return canCreateFile(newName);
    }

    public boolean canCreateFile(@NotNull final String newName) {
        if (project != null) {
            // not just changing file name case
            final VirtualFile parent = getVirtualParent();
            if (parent != null) {
                if (parent.findChild(newName) == null) {
                    return true;
                    //final boolean[] result = new boolean[1];
                    //
                    //Application application = ApplicationManager.getApplication();
                    //
                    //if (!application.isWriteAccessAllowed()) return true;
                    //
                    //application.runWriteAction(new Runnable() {
                    //    @Override
                    //    public void run() {
                    //        try {
                    //            VirtualFile newVirtualFile = parent.createChildData(this, newName);
                    //            result[0] = true;
                    //            try {
                    //                newVirtualFile.delete(this);
                    //            } catch (IOException ignore) {
                    //                logger.info("IOException on delete " + newName, ignore);
                    //            }
                    //        } catch (IOException ignore) {
                    //            // can't create it, so we remove it
                    //            logger.info("IOException on create " + newName, ignore);
                    //        }
                    //    }
                    //});
                    //return result[0];
                }
            }
        }
        return false;
    }
}
