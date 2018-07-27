# Fruit
果实

# 注意
    library中有两种实现， 详情可看Demo
    1.带有回收复用的实现
    2.不带有复用的实现
    
# 使用
## 1. FruitView
    使用方法跟RecyclerView一样
    
## 2. SimpleFruitView（自定义ViewGroup实现， 抽象需要使用者根据实际情况继承实现）
        
        // 绑定数据
        protected abstract void bindData(View view, int index, T data);
        
        // 自定义布局
        protected abstract View createFruitView(ViewGroup NestedScrollingParent, T data);
        
        // 自定义位置（暂支持5中）
        protected int getFruitPattern(int index, T data) {
            return SimpleViewHolder.POSITION_PATTERN_AUTO;
        }
        
        // 速度
        protected float getStep(int index, T data) {
            return 2;
        }
        
        // 峰值
        protected float getPeakValue(int index, T data) {
            return 10;
        }
        
        // 自己的实体类是否相同， 需要用户自己指定
        protected abstract boolean isEqual(T t, T tt);
    
        // 退出的动画
        protected Animator getQuitAnimation(int position, T t, View view) {
            return null;
        }
        
        // 进入的动画
        protected Animator getAddAnimation(int index, T data, View view) {
            return null;
        }
