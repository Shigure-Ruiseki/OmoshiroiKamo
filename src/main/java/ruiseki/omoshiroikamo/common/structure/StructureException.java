package ruiseki.omoshiroikamo.common.structure;

/**
 * 構造体設定のエラーを表すカスタム例外
 */
public class StructureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorType type;
    private final String fileName;
    private final String entryName;

    /**
     * エラータイプ
     */
    public enum ErrorType {

        FILE_NOT_FOUND("File not found"),
        PARSE_ERROR("JSON parse error"),
        INVALID_FORMAT("Invalid format"),
        MISSING_FIELD("Required field missing"),
        INVALID_BLOCK("Invalid block specification"),
        EMPTY_STRUCTURE("Empty structure definition"),
        LOAD_FAILED("Load failed"),
        WRITE_FAILED("Write failed"),
        UNKNOWN("Unknown error");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 基本コンストラクタ
     */
    public StructureException(ErrorType type, String message) {
        super(message);
        this.type = type;
        this.fileName = null;
        this.entryName = null;
    }

    /**
     * ファイル情報付きコンストラクタ
     */
    public StructureException(ErrorType type, String fileName, String message) {
        super(message);
        this.type = type;
        this.fileName = fileName;
        this.entryName = null;
    }

    /**
     * 全情報付きコンストラクタ
     */
    public StructureException(ErrorType type, String fileName, String entryName, String message) {
        super(message);
        this.type = type;
        this.fileName = fileName;
        this.entryName = entryName;
    }

    /**
     * 原因付きコンストラクタ
     */
    public StructureException(ErrorType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.fileName = null;
        this.entryName = null;
    }

    /**
     * 全情報+原因付きコンストラクタ
     */
    public StructureException(ErrorType type, String fileName, String entryName, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.fileName = fileName;
        this.entryName = entryName;
    }

    public ErrorType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public String getEntryName() {
        return entryName;
    }

    /**
     * フォーマット済みのエラーメッセージを取得
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[")
            .append(type.name())
            .append("] ");

        if (fileName != null) {
            sb.append(fileName);
            if (entryName != null) {
                sb.append(" > ")
                    .append(entryName);
            }
            sb.append(": ");
        }

        sb.append(getMessage());
        return sb.toString();
    }

    // ===== ファクトリーメソッド =====

    public static StructureException fileNotFound(String fileName) {
        return new StructureException(ErrorType.FILE_NOT_FOUND, fileName, "Structure file not found: " + fileName);
    }

    public static StructureException parseError(String fileName, String detail) {
        return new StructureException(ErrorType.PARSE_ERROR, fileName, "Failed to parse: " + detail);
    }

    public static StructureException parseError(String fileName, String detail, Throwable cause) {
        return new StructureException(ErrorType.PARSE_ERROR, fileName, null, "Failed to parse: " + detail, cause);
    }

    public static StructureException invalidFormat(String fileName, String detail) {
        return new StructureException(ErrorType.INVALID_FORMAT, fileName, "Invalid format: " + detail);
    }

    public static StructureException missingField(String fileName, String entryName, String fieldName) {
        return new StructureException(
            ErrorType.MISSING_FIELD,
            fileName,
            entryName,
            "Missing required field: " + fieldName);
    }

    public static StructureException invalidBlock(String fileName, String entryName, String blockId) {
        return new StructureException(ErrorType.INVALID_BLOCK, fileName, entryName, "Invalid block ID: " + blockId);
    }

    public static StructureException emptyStructure(String fileName, String entryName) {
        return new StructureException(
            ErrorType.EMPTY_STRUCTURE,
            fileName,
            entryName,
            "Structure has no layers defined");
    }

    public static StructureException loadFailed(String fileName, Throwable cause) {
        return new StructureException(ErrorType.LOAD_FAILED, fileName, null, "Failed to load structure file", cause);
    }
}
