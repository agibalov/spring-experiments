package me.loki2302.jdbc.querydsl;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import com.mysema.query.sql.PrimaryKey;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

public class QUserRow extends RelationalPathBase<QUserRow> {
    private static final long serialVersionUID = 1209561150476404798L;
    
    public static final QUserRow userRow = new QUserRow("UserRow");
    public final NumberPath<Integer> id = createNumber("ID", Integer.class); // IMPORTANT: "ID", not "Id" or "id".
    public final StringPath name = createString("Name");
    
    public final PrimaryKey<QUserRow> pk = createPrimaryKey(id);
    
    public QUserRow(String variable) {
        super(QUserRow.class, forVariable(variable), "PUBLIC", "Users");
    }
    
    @SuppressWarnings("all")
    public QUserRow(Path<? extends QUserRow> path) {
        super((Class)path.getType(), path.getMetadata(), "PUBLIC", "Users");
    }

    public QUserRow(PathMetadata<?> metadata) {
        super(QUserRow.class, metadata, "PUBLIC", "Users");
    }        
}