package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.WishlistSectionDto;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.entities.WishlistSection;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.repositories.WishlistSectionRepository;
import com.lcwd.electronic.store.services.WishlistSectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistSectionServiceImpl implements WishlistSectionService {

    @Autowired
    private WishlistSectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public WishlistSectionDto createSection(WishlistSectionDto sectionDto) {
        User user = userRepository.findById(sectionDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + sectionDto.getUserId()));

        WishlistSection section = WishlistSection.builder()
                .name(sectionDto.getName())
                .description(sectionDto.getDescription())
                .user(user)
                .build();

        WishlistSection savedSection = sectionRepository.save(section);
        return mapper.map(savedSection, WishlistSectionDto.class);
    }

    @Override
    public WishlistSectionDto updateSection(Long sectionId, WishlistSectionDto sectionDto) {
        WishlistSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        section.setName(sectionDto.getName());
        section.setDescription(sectionDto.getDescription());

        WishlistSection updatedSection = sectionRepository.save(section);
        return mapper.map(updatedSection, WishlistSectionDto.class);
    }

    @Override
    public void deleteSection(Long sectionId) {
        WishlistSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        sectionRepository.delete(section);
    }

    @Override
    public List<WishlistSectionDto> getUserSections(String userId) {
        List<WishlistSection> sections = sectionRepository.findByUserUserId(userId);
        return sections.stream()
                .map(section -> mapper.map(section, WishlistSectionDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public WishlistSectionDto getSection(Long sectionId) {
        WishlistSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        return mapper.map(section, WishlistSectionDto.class);
    }
}