package org.alfresco.repo.search.impl.querymodel.impl.db.functions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQuery;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderJoinCommand;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderJoinCommandType;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderPredicatePartCommand;
import org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderPredicatePartCommandType;
import org.alfresco.repo.search.impl.querymodel.impl.db.ParentSupport;
import org.alfresco.repo.search.impl.querymodel.impl.db.PropertySupport;
import org.alfresco.repo.search.impl.querymodel.impl.db.TypeSupport;
import org.alfresco.repo.search.impl.querymodel.impl.db.UUIDSupport;
import org.alfresco.repo.search.impl.querymodel.impl.functions.NotEquals;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * @author Andy
 *
 */
public class DBNotEquals extends NotEquals implements DBQueryBuilderComponent
{
    DBQueryBuilderComponent builderSupport;

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#isSupported()
     */
    @Override
    public boolean isSupported()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#prepare(org.alfresco.service.namespace.NamespaceService, org.alfresco.service.cmr.dictionary.DictionaryService, org.alfresco.repo.domain.qname.QNameDAO, org.alfresco.repo.domain.node.NodeDAO, java.util.Set, java.util.Map, org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext)
     */
    @Override
    public void prepare(NamespaceService namespaceService, DictionaryService dictionaryService, QNameDAO qnameDAO, NodeDAO nodeDAO, TenantService tenantService, Set<String> selectors,
            Map<String, Argument> functionArgs, FunctionEvaluationContext functionContext, boolean supportBooleanFloatAndDouble)
    {
        setPropertyAndStaticArguments(functionArgs);
        Serializable staticValue = getStaticArgument().getValue(functionContext);

        if (getPropertyName().equals(PropertyIds.PARENT_ID))
        {
            ParentSupport parentSupport = new ParentSupport();
            String id = (String) staticValue;
            parentSupport.setDbid(DBQuery.getDbid(id, nodeDAO, tenantService));
            parentSupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTEQUALS);
            builderSupport = parentSupport;
        }
        else if (getPropertyName().equals(PropertyIds.OBJECT_ID))
        {
            UUIDSupport uuidSupport = new UUIDSupport();
            uuidSupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTEQUALS);
            uuidSupport.setUuid(DBQuery.getUUID((String)staticValue));
            builderSupport = uuidSupport;
        }
        else if (getPropertyName().equals(PropertyIds.OBJECT_TYPE_ID))
        {
            TypeSupport typeSupport = new TypeSupport();
            String typeName = functionContext.getAlfrescoTypeName((String)staticValue);
            typeSupport.setQnameIds(DBQuery.findTypeIds(typeName, namespaceService, dictionaryService, qnameDAO, true));
            typeSupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTIN);
            builderSupport = typeSupport;
        }
        else if (getPropertyName().equals(PropertyIds.BASE_TYPE_ID))
        {
            TypeSupport typeSupport = new TypeSupport();
            String typeName = functionContext.getAlfrescoTypeName((String)staticValue);
            typeSupport.setQnameIds(DBQuery.findTypeIds(typeName, namespaceService, dictionaryService, qnameDAO, false));
            typeSupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTIN);
            builderSupport = typeSupport;
        }
        else if (getPropertyName().equals(PropertyIds.CONTENT_STREAM_MIME_TYPE))
        {
            PropertySupport propertySupport = new PropertySupport();
            propertySupport.setValue(staticValue.toString());
            
            QName basePropertyQName = QName.createQName(DBQuery.expandQName(functionContext.getAlfrescoPropertyName(getPropertyName()), namespaceService));
            propertySupport.setPropertyQName(basePropertyQName);
            propertySupport.setPropertyDataType(DBQuery.getDataTypeDefinition(dictionaryService, basePropertyQName));
            propertySupport.setPair(qnameDAO.getQName(basePropertyQName));
            propertySupport.setJoinCommandType(DBQueryBuilderJoinCommandType.CONTENT_MIMETYPE);
            propertySupport.setFieldName("mimetype_str");
            propertySupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTEQUALS);
            propertySupport.setLuceneFunction(functionContext.getLuceneFunction(getFunctionArgument()));
            builderSupport = propertySupport;
        }
        else if (getPropertyName().equals(PropertyIds.CONTENT_STREAM_LENGTH))
        {
            PropertySupport propertySupport = new PropertySupport();
            propertySupport.setValue(staticValue.toString());
            
            QName basePropertyQName = QName.createQName(DBQuery.expandQName(functionContext.getAlfrescoPropertyName(getPropertyName()), namespaceService));
            propertySupport.setPropertyQName(basePropertyQName);
            propertySupport.setPropertyDataType(DBQuery.getDataTypeDefinition(dictionaryService, basePropertyQName));
            propertySupport.setPair(qnameDAO.getQName(basePropertyQName));
            propertySupport.setJoinCommandType(DBQueryBuilderJoinCommandType.CONTENT_URL);
            propertySupport.setFieldName("content_size");
            propertySupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTEQUALS);
            propertySupport.setLuceneFunction(functionContext.getLuceneFunction(getFunctionArgument()));
            builderSupport = propertySupport;
        }
        else
        {
            PropertySupport propertySupport = new PropertySupport();
            propertySupport.setValue(staticValue.toString());
            
            QName propertyQName = QName.createQName(DBQuery.expandQName(functionContext.getAlfrescoPropertyName(getPropertyName()), namespaceService));
            propertySupport.setPropertyQName(propertyQName);
            propertySupport.setPropertyDataType(DBQuery.getDataTypeDefinition(dictionaryService, propertyQName));
            propertySupport.setPair(qnameDAO.getQName(propertyQName));
            propertySupport.setJoinCommandType(DBQuery.getJoinCommandType(propertyQName));
            propertySupport.setFieldName(DBQuery.getFieldName(dictionaryService, propertyQName, supportBooleanFloatAndDouble));
            propertySupport.setCommandType(DBQueryBuilderPredicatePartCommandType.NOTEQUALS);
            propertySupport.setLuceneFunction(functionContext.getLuceneFunction(getFunctionArgument()));
            builderSupport = propertySupport;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#buildJoins(java.util.Map, java.util.List)
     */
    @Override
    public void buildJoins(Map<QName, DBQueryBuilderJoinCommand> singleJoins, List<DBQueryBuilderJoinCommand> multiJoins)
    {
        builderSupport.buildJoins(singleJoins, multiJoins);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.db.DBQueryBuilderComponent#buildPredicateCommands(java.util.List)
     */
    @Override
    public void buildPredicateCommands(List<DBQueryBuilderPredicatePartCommand> predicatePartCommands)
    {
        builderSupport.buildPredicateCommands(predicatePartCommands);
    }

}
