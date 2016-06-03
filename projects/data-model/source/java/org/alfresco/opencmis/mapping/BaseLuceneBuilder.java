package org.alfresco.opencmis.mapping;

import java.io.Serializable;
import java.util.Collection;

import org.alfresco.opencmis.dictionary.CMISPropertyLuceneBuilder;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserExpressionAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;

/**
 * Base class for all property lucene builders
 * 
 * @author davidc
 */
public class BaseLuceneBuilder implements CMISPropertyLuceneBuilder
{
    /**
     * Construct
     * 
     */
    protected BaseLuceneBuilder()
    {
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E 
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneIn(LuceneQueryParserAdaptor<Q, S, E> lqpa, Collection<Serializable> values, Boolean not, PredicateMode mode) throws E
    {
        LuceneQueryParserExpressionAdaptor<Q, E> expressionAdaptor = lqpa.getExpressionAdaptor();
        for(Serializable value : values)
        {
            expressionAdaptor.addOptional(buildLuceneEquality(lqpa, value, mode, LuceneFunction.FIELD));
        }
        if(not)
        {
            return expressionAdaptor.getNegatedQuery();
        }
        else
        {
            return expressionAdaptor.getQuery();
        }
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        return lqpa.getNegatedQuery(buildLuceneEquality(lqpa, value, mode, luceneFunction));
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLuceneFieldName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa) throws E
    {
        throw new UnsupportedOperationException();
    }
}
