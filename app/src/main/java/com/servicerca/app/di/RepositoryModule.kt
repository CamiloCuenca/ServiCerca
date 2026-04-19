package com.servicerca.app.di

import com.servicerca.app.data.repository.ChatRepositoryImpl
import com.servicerca.app.data.repository.CommentRepositoryImpl
import com.servicerca.app.data.repository.ReservationRepositoryImpl
import com.servicerca.app.data.repository.ServiceRepositoryImpl
import com.servicerca.app.data.repository.UserRepositoryImpl
import com.servicerca.app.data.repository.NotificationRepositoryImpl
import com.servicerca.app.domain.repository.ChatRepository
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(
        reservationRepositoryImpl: ReservationRepositoryImpl
    ): ReservationRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

}
