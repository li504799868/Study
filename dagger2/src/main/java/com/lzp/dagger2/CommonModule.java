package com.lzp.dagger2;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by li.zhipeng on 2019/4/9.
 *
 * 数据来源类
 */
@Module
public class CommonModule{

    private ICommonView iView;
    public CommonModule(ICommonView iView){
        this.iView = iView;
    }

    /**
     * 该方法会作为IcommonView的提供者
     * */
    @Provides
    @ActivityScope
    public ICommonView provideIcommonView(){
        return this.iView;
    }

}
